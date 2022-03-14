# NOT FINISHED BECAUSE DICT BASED IMPLEMENTATION SEAMS TO BE EFFICENT ENOUGHT

file = open(ARGS[1], "r")

# using dicts here is suboptimal 
function occurrences(file::IO)
    num_occurrences = zeros(Int64, typemax(UInt8) + 1)
    num_occurrences_conditional = zeros(Int64, typemax(UInt8) + 1, typemax(UInt8) + 1)
    local previous_byte::UInt8

    first_byte = read(file, UInt8)
    num_occurrences[first_byte + 1] = 1
    num_occurrences_conditional[1, first_byte + 1] = 1
    previous_byte = first_byte
    all_occurrences::Int64 = 1

    for byte in readeach(file, UInt8)
        num_occurrences[byte + 1] += 1
        num_occurrences_conditional[previous_byte + 1, byte + 1] += 1
        previous_byte = byte
        all_occurrences += 1
    end
    return (num_occurrences, num_occurrences_conditional, all_occurrences)
end

function entropy(num_occurrences::Vector{Int64}, all_occurrences::Int64)::Float64
    local entropy::Float64 = 0
    for i in 0:typemax(UInt8)
        if (num_occurrences[i + 1]) > 0      
            propabilty::Float64 = num_occurrences[i + 1] / all_occurrences
            entropy -=  propabilty * log2(propabilty)
        end
    end
    return entropy
end

function conditional_entropy(num_occurrences_conditional::Matrix{Inf64}, all_occurrences::Int64)::Float64

end

(num_occurrences, _, all_occurrences) = occurrences(file)

println("entropia: ", entropy(num_occurrences, all_occurrences))
# println(num_occurrences)