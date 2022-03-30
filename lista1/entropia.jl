file = open(ARGS[1], "r")

# using dicts here is suboptimal 
function occurrences(file::IO)::Tuple{Dict{UInt8,Int64},Dict{Tuple{UInt8,UInt8},Int64},Int64}
    num_occurrences = Dict{UInt8,Int64}()
    num_occurrences_conditional = Dict{Tuple{UInt8,UInt8},Int64}()
    local previous_byte::UInt8

    first_byte = read(file, UInt8)
    num_occurrences[first_byte] = 1
    num_occurrences_conditional[(0, first_byte)] = 1
    previous_byte = first_byte
    all_occurrences::Int64 = 1


    for byte in readeach(file, UInt8)
        if (haskey(num_occurrences, byte))
            num_occurrences[byte] += 1
            if (haskey(num_occurrences_conditional, (previous_byte, byte)))
                num_occurrences_conditional[(previous_byte, byte)] += 1
            else
                num_occurrences_conditional[(previous_byte, byte)] = 1
            end
        else
            num_occurrences[byte] = 1
        end
        previous_byte = byte
        all_occurrences += 1
    end

    return (num_occurrences, num_occurrences_conditional, all_occurrences)
end

function entropy(num_occurrences::Dict{UInt8,Int64}, all_occurrences::Int64)::Float64
    local entropy::Float64 = 0
    for (key, value) in num_occurrences
        probability::Float64 = value / all_occurrences
        entropy -= probability * log2(probability)
    end
    return entropy
end

function conditional_entropy(num_occurrences::Dict{UInt8,Int64}, num_occurrences_conditional::Dict{Tuple{UInt8,UInt8},Int64}, all_occurrences::Int64)::Float64
    if (!haskey(num_occurrences, 0))
        num_occurrences[0] = 1
        all_occurrences += 1
    end
    entropy_conditional = Dict{UInt8,Float64}() # H(Y|x)
    for ((x, y), num) in num_occurrences_conditional
        probability::Float64 = num / num_occurrences[x] # P(y|x)
        if (haskey(entropy_conditional, x))
            entropy_conditional[x] -= probability * log2(probability)
        else
            entropy_conditional[x] = -probability * log2(probability)
        end
    end
    entropy::Float64 = 0
    for (key, value) in num_occurrences
        x_probability::Float64 = value / all_occurrences # P(x)
        entropy += x_probability * entropy_conditional[key]
    end
    return entropy
end

(num_occurrences, num_occurrences_conditional, all_occurrences) = occurrences(file)

ent = entropy(num_occurrences, all_occurrences)
cond_ent = conditional_entropy(num_occurrences, num_occurrences_conditional, all_occurrences)
var = ent - cond_ent
# println(num_occurrences)
println(ent)
println(cond_ent)
# println("różnica: ", var)
close(file)